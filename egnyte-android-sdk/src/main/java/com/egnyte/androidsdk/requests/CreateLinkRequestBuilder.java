package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.requests.CreateLinkRequest.Protection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.egnyte.androidsdk.requests.CreateLinkRequest.Accessibility;
import static com.egnyte.androidsdk.requests.CreateLinkRequest.Type;

/**
 * Base class for building Create Link requests
 * @param <T> {@link CreateFileLinkRequestBuilder} or {@link CreateFolderLinkRequestBuilder}
 */
public abstract class CreateLinkRequestBuilder<T extends CreateLinkRequestBuilder> {

    private String path;
    private com.egnyte.androidsdk.requests.CreateLinkRequest.Type type;
    private Accessibility accessibility;
    private EmailLink emailLink;
    private Boolean notify;
    private Expiration expiration;

    CreateLinkRequestBuilder(String path, Type type, Accessibility accessibility) {
        this.path = path;
        this.type = type;
        this.accessibility = accessibility;
    }

    /**
     * Configures if the link should be sent as well as parameters of sending
     * @param emailLink {@link EmailLink} configuring sending of email, might be null
     * @return the same builder
     */
    public T setEmailLink(EmailLink emailLink) {
        this.emailLink = emailLink;
        return (T) this;
    }

    /**
     * If true is set, link creator will be notified via email when link is accessed
     * @param notify whether creator should be notified by email when link is accessed
     * @return the same builder
     */
    public T setNotify(Boolean notify) {
        this.notify = notify;
        return (T) this;
    }

    /**
     * Sets when link should expire, {@see {@link Expiration#byClicks(int)} or {@link Expiration#byDate(Date)}}
     * @param expiration {@link Expiration} describing when link should expire, might be null
     * @return the same builder
     */
    public T setExpiration(Expiration expiration) {
        this.expiration = expiration;
        return (T) this;
    }

    CreateLinkRequest build(Boolean linkToCurrent, Protection protection, Boolean addFileName, Boolean folderPerReceipent) {
        Boolean sendEmail = emailLink == null ? null : true;
        List<String> recipients = emailLink == null ? null : emailLink.recipients;
        String message = emailLink == null ? null : emailLink.message;
        Boolean copyMe = emailLink == null ? null : emailLink.copyMe;

        String expiryDate = null;
        Integer expiryClicks = null;

        if (expiration != null) {
            if (expiration.type == Expiration.Type.DATE) {
                expiryDate = (String) expiration.value;
            } else if (expiration.type == Expiration.Type.CLICKS) {
                expiryClicks = (Integer) expiration.value;
            }
        }

        return new CreateLinkRequest(path, type, accessibility, sendEmail, recipients, message, copyMe, notify, linkToCurrent, expiryDate, expiryClicks, protection, addFileName, folderPerReceipent);
    }

    public abstract CreateLinkRequest build();

    /**
     * Class representing settings of sending created link
     */
    public static class EmailLink {
        private final List<String> recipients;
        private final String message;
        private final Boolean copyMe;

        /**
         * @param recipients list of email addresses that will receive link
         * @param message message attached to sent email
         * @param copyMen whether creator of link should receive copy of email

         */
        public EmailLink(List<String> recipients, String message, Boolean copyMe) {
            this.recipients = recipients;
            this.message = message;
            this.copyMe = copyMe;
        }
    }

    /**
     * Class representing expiration setting for created link
     */
    public static class Expiration {

        private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        private enum Type {
            DATE, CLICKS
        }

        private final Type type;
        private final Object value;

        /**
         * Expire by given date, note that it should be in the future
         * @param expirationDate future expiration date
         * @return
         */
        public static Expiration byDate(Date expirationDate) {
            return new Expiration(Type.DATE, format.format(expirationDate));
        }

        /**
         * Expire after given number of accesses. Note that it should be 1 - 10 value.
         * @param count 1 - 10 value
         * @return
         */
        public static Expiration byClicks(int count) {
            return new Expiration(Type.CLICKS, count);
        }

        private Expiration(Type by, Object value) {
            this.type = by;
            this.value = value;
        }
    }
}