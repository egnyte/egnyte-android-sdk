package com.egnyte.androidsdk.requests;

import static com.egnyte.androidsdk.requests.CreateLinkRequest.Type.FOLDER;
/**
 * Use this class for building an {@link CreateLinkRequest} for creating a folder to file
 */
public class CreateFolderLinkRequestBuilder extends CreateLinkRequestBuilder<CreateFolderLinkRequestBuilder> {

    /**
     * @param path path to file
     * @param accessibility {@link com.egnyte.androidsdk.requests.CreateLinkRequest.Accessibility}
     */
    public CreateFolderLinkRequestBuilder(String path, CreateLinkRequest.Accessibility accessibility) {
        super(path, FOLDER, accessibility);
    }

    @Override
    public CreateLinkRequest build() {
        return build(null, null, null, null);
    }
}
