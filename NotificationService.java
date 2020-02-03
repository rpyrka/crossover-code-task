package com.crossover;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.crossover.Subscription.SubscriptionType;


class NotificationService {

	/**
	 * SLF4J RELATED LOGGER
	 */
	private final Logger log = LoggerFactory.getLogger(NotificationService.class);

	
	final SubscriptionDAO dao;
	
	final NotificationProviderFactory notificationProviderFactory;
	
	long processingTime = 0l;

	public NotificationService(SubscriptionDAO dao,
			NotificationProviderFactory notificationProviderFactory) {
		this.dao = dao;
		this.notificationProviderFactory = notificationProviderFactory;
	}

	public void sendQuaterlySubscriptionNotification() {
		processingTime = System.currentTimeMillis();
		List<Subscription> subscriptions = (List<Subscription>) dao.getSubscriptions();
		for(Subscription subscription : subscriptions) {
			if(subscription.getType().equals(SubscriptionType.QUATERLY)) {
				Notification notification = prepareNotification(subscription);
				EmailAdapter emailAdapter = notificationProviderFactory.createNotificationProvider(subscription);
				if(emailAdapter.sendEmail(notification)) {
					notification.sentSuccessfully();
					subscription.addNotification(notification);
					dao.saveSubscription(subscription);
				}
			
			}
		}
		
		log.debug("Sending notifitions took: "+(System.currentTimeMillis() - processingTime));
	}
	
	private Notification prepareNotification(Subscription subscription){
		Notification notification;
		/**
		 * code responsible for preparing notification
		 */
		return notification;
	}
}
class SmtpException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3136809650565776344L;
	
	private final String msg;
	public SmtpException(Exception e){
		this.msg=e.getMessage();
	}
}


class EmailAdapter {
	public boolean sendEmail(Notification notification) {
		boolean success = false;
		try {
		/**
		 * code responsible for sending e-mail over SMTP
		 */
		} catch (Exception e) {
			throw new SmtpException(e);
		}
		return success;
		
	}
}

class NotificationProviderFactory {
	public EmailAdapter createNotificationProvider(Subscription subscription) {
		return new EmailAdapter();
	}
}
class Notification {
	
	String message;
	boolean successfullySent;
	public void sentSuccessfully(){
		successfullySent=true;
	}
	/**
	 * SOME OTHER FIELDS
	 */
}

class SubscriptionDAO {

	public void saveSubscription(Subscription s) {
		/**
		 * SOME CODE RESPONSIBLE FOR DB OPERATIONS. IRRELEVANT
		 */
	}

	public LinkedList<Subscription> getSubscriptions() {
		LinkedList<Subscription> list = null;
		/**
		 * SOME CODE RESPONSIBLE FOR DB OPERATIONS.
		 */
		
		
		return list;
	}
}

class Subscription {
	private SubscriptionType type;
	private List<Notification> notifications = new LinkedList<Notification>();

	/**
	 * SOME OTHER FIELDS
	 */
	public SubscriptionType getType() {
		return type;
	}
	public void addNotification(Notification notification) {
		notifications.add(notification);
	}
	
	enum SubscriptionType {
		MONTHLY,QUATERLY,YEARLY;
	}
}


