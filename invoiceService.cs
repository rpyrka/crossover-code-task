using EmailClient;
using InvoiceMicroService.Configuration;
using InvoiceMicroService.Repository;
using ServiceMicroServiceClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Threading.Tasks;
using UserMicroServiceClient;
using System.Buffers;

namespace InvoiceMicroService
{
    public class InvoiceService : IInvoiceService
    {
        private IEmailClient _emailClient;
        private InvoiceEmailConfiguration _invoiceEmailConfiguration;
        private IUserMicroServiceClient _userClient;
        private IServiceMicroServiceClient _serviceClient;
        private IDataBaseContext _dataBaseContext;

        public InvoiceService(IEmailClient emailClient, IUserMicroServiceClient userClient, IServiceMicroServiceClient serviceClient,
            InvoiceEmailConfiguration invoiceEmailConfiguration, IDataBaseContext dataBaseContext)
        {
            _emailClient = emailClient;
            _userClient = userClient;
            _serviceClient = serviceClient;
            _invoiceEmailConfiguration = invoiceEmailConfiguration;
            _dataBaseContext = dataBaseContext;
        }

        public async Task InvoiceUsers(DateTime invoicePeriodFrom, DateTime invoicePeriodTo)
        {
            var users = await _userClient.Get();

            foreach (var user in users)
            {
                var services = await _serviceClient.Get(user.UniqueIdentifier);

                var invoiceAttachments = new List<string>();

                foreach (var service in services)
                {
                  var invoiceRepository = new InvoiceRepository(_dataBaseContext);
                  var invoices = invoiceRepository.Get().Where(s => s.ServiceId == service.UniqueIdentifier && s.From >= invoicePeriodFrom && s.To <= invoicePeriodTo);

                    foreach (var invoice in invoices)
                    {
                        invoiceAttachments.Add(invoice.FilePath);
                    }
                }

                await _emailClient.Send(_invoiceEmailConfiguration.From, new MailAddress(user.Email),
                                _invoiceEmailConfiguration.Subject, _invoiceEmailConfiguration.Body, invoiceAttachments.Select(s => new Attachment(s)));
            }
        }
    }
}