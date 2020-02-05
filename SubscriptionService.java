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

	public void prepareQuaterlySubscriptionNotifications() {
		processingTime = System.currentTimeMillis();
		List<Subscription> subscriptions = (List<Subscription>) dao.getSubscriptions();
		InputStream attachmentInputStream = null;

		try {
			for(Subscription subscription : subscriptions) {
				if(subscription.getType().equals(SubscriptionType.QUATERLY)) {
					Path path = Paths.get(subscription.getTemplateFileName());
					attachmentInputStream = Files.newInputStream(path);
					Notification notification = prepareNotification(subscription);
					EmailAdapter emailAdapter = notificationProviderFactory.createNotificationProvider(subscription);
					if(emailAdapter.saveEmail(notification, attachmentInputStream)) {
						notification.savedSuccessfully();
						subscription.addNotification(notification);
						dao.saveSubscription(subscription);
					}
				}
			}
			attachmentInputStream.close();
		} catch (IOException e) {
			/**
			 * Error handling code, including attachmentInputStream.close();
			 */
		} 		
		log.debug("Sending notifitions took: "+(System.currentTimeMillis() - processingTime));
	}
	
	private Notification prepareNotification(Subscription subscription){
		Notification notification = null;
		
		/**
		 * code responsible for preparing notification
		 */
		return notification;
	}
}
class MailSavingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3136809650565776344L;
	
	private final String msg;
	public MailSavingException(Exception e){
		this.msg=e.getMessage();
	}
}


class EmailAdapter {
	public boolean saveEmail(Notification notification, InputStream attachmentInputStream) {
		boolean success = false;
		try {
		/**
		 * code responsible for saving e-mail over SMTP which are later sent by 
		 * batch, robust e-mail sending job
		 */
		} catch (Exception e) {
			throw new MailSavingException(e);
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
	boolean successfullySaved;
	public void savedSuccessfully(){
		successfullySaved=true;
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
	private String templateFileName;
;
	/**
	 * SOME OTHER FIELDS
	 */
	public SubscriptionType getType() {
		return type;
	}
	public void addNotification(Notification notification) {
		notifications.add(notification);
	}
	public String getTemplateFileName(){
		return templateFileName;
	}
	
	enum SubscriptionType {
		MONTHLY,QUATERLY,YEARLY;
	}
}