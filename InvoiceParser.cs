using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using Newtonsoft.Json;

namespace EWS.EngHub.Managers
{
    #region Object structures

    public interface IInvoiceProviderService
    {
        public IEnumerable<Invoice> GetInvoices();
        public void CommitInvoiceData(Invoice invoiceItem);
    }

    public interface ITaxInfoServiceProvider
    {
        public decimal TaxAmount(AddressInfo billingAddress);
    }

    public class AddressInfo
    {
        public string Address { get; set; }
        public string City { get; set; }
        public string Zip { get; set; }
        public string State { get; set; }
        public string ZipCode { get; set; }
    }

    public class Invoice
    {
        public string InvoiceNumber { get; set; }
        public string DateCreated { get; set; }
        public int CorrelationId { get; set; }
        public int AmountInCents { get; set; }
        public int DiscountAmountInCents { get; set; }
        public decimal SavingPercent => AmountInCents / DiscountAmountInCents * 100;
        public decimal TaxAmount { get; set; }
        public DateTime ProcessedDate { get; set; }
        public AddressInfo BillingAddress { get; set; }
        public AddressInfo ShippingAddress { get; set; }
    }

    #endregion

    public class InvoiceProcessingJob
    {
        private readonly IInvoiceProviderService _invoiceProviderService;
        private readonly ITaxInfoServiceProvider _taxInfoService;
        private Invoice invoice;
        public bool SavingApplied => invoice.SavingPercent > 0;

        public bool IsInvoiceValid => invoice.AmountInCents != 0;

        public InvoiceProcessingJob(ITaxInfoServiceProvider taxInfoService, IInvoiceProviderService invoiceProviderService)
        {
            _taxInfoService = taxInfoService;
            _invoiceProviderService = invoiceProviderService;
        }

        private void GenerateInvoiceFile(Invoice invoiceInstance)
        {
            invoice = invoiceInstance;
            var taxAmount = CalculateTax(invoice);
            invoice.TaxAmount = taxAmount;

            using var sw = new StreamWriter($"{invoiceInstance.InvoiceNumber}.json", true, Encoding.Default);
            var invoiceFileContent = JsonConvert.SerializeObject(invoiceInstance);
            sw.WriteLine(invoiceFileContent);

            _invoiceProviderService.CommitInvoiceData(invoice);
        }

        public void ProcessInvoicesList()
        {
            _invoiceProviderService.GetInvoices().ToList().ForEach(s =>
            {
                s.TaxAmount = CalculateTax(s);
                GenerateInvoiceFile(s);
            });
        }

        private decimal CalculateTax(Invoice invoiceItem)
        {
            return _taxInfoService.TaxAmount(invoiceItem.BillingAddress);
        }
    }
}