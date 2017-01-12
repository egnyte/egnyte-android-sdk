package com.egnyte.androidsdk.requests;

/**
 * Use this class for building an {@link CreateLinkRequest} for creating a folder to file
 */
public class CreateUploadLinkRequestBuilder extends CreateLinkRequestBuilder<CreateUploadLinkRequestBuilder> {

    private Boolean folderPerReceipent;

    /**
     * @param path path to file
     */
    public CreateUploadLinkRequestBuilder(String path) {
        super(path, CreateLinkRequest.Type.UPLOAD, null);
    }

    /**
     * If true then each recipient's uploaded data will be put into a separate folder.
     * @param folderPerReceipent
     * @return
     */
    public CreateUploadLinkRequestBuilder setFolderPerReceipent(Boolean folderPerReceipent) {
        this.folderPerReceipent = folderPerReceipent;
        return this;
    }

    @Override
    public CreateLinkRequest build() {
        return build(null, null, null, folderPerReceipent);
    }
}
