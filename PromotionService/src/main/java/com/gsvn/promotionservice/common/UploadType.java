package com.gsvn.promotionservice.common;


import lombok.Getter;

@Getter
public enum UploadType {
    STAFF_AVATAR("staff/avatars", 300, 300, true),
    PRODUCT_SKU("products/sku", 600, 600, true),
    PRODUCT_GALLERY("products/gallery", 1000, 1000, true),
    DOCUMENT("documents", 0, 0, false);

    private final String folder;
    private final int width;
    private final int height;
    private final boolean requireResize;

    UploadType(String folder, int width, int height, boolean requireResize) {
        this.folder = folder;
        this.width = width;
        this.height = height;
        this.requireResize = requireResize;
    }

    public static UploadType fromString(String type) {
        try {
            return UploadType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DOCUMENT;
        }
    }
}