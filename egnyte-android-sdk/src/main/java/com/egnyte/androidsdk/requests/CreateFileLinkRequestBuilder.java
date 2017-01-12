package com.egnyte.androidsdk.requests;

/**
 * Use this class for building an {@link CreateLinkRequest} for creating a link to file
 */
public class CreateFileLinkRequestBuilder extends CreateLinkRequestBuilder<CreateFileLinkRequestBuilder> {

    private Boolean linkToCurrent;
    private CreateLinkRequest.Protection protection;
    private Boolean addFileName;

    /**
     * @param path path to file
     * @param accessibility {@link com.egnyte.androidsdk.requests.CreateLinkRequest.Accessibility}
     */
    public CreateFileLinkRequestBuilder(String path, CreateLinkRequest.Accessibility accessibility) {
        super(path, CreateLinkRequest.Type.FILE, accessibility);
    }

    /**
     * Sets whether link should always link to current version of file
     * @param linkToCurrent whether link should always link to current version of file
     * @return the same builder
     */
    public CreateFileLinkRequestBuilder setLinkToCurrent(Boolean linkToCurrent) {
        this.linkToCurrent = linkToCurrent;
        return this;
    }

    /**
     * If set to {@link com.egnyte.androidsdk.requests.CreateLinkRequest.Protection#PREVIEW} will create a preview-only link to the file
     * @param protection {@see {@link com.egnyte.androidsdk.requests.CreateLinkRequest.Protection}}
     * @return
     */
    public CreateFileLinkRequestBuilder setProtection(CreateLinkRequest.Protection protection) {
        this.protection = protection;
        return this;
    }

    /**
     * If true then the filename will be appended to the end of the line
     * @param addFileName whether filename should be appended to the end of the line
     * @return
     */
    public CreateFileLinkRequestBuilder setAddFileName(boolean addFileName) {
        this.addFileName = addFileName;
        return this;
    }

    @Override
    public CreateLinkRequest build() {
        return build(linkToCurrent, protection, addFileName, null);
    }
}
