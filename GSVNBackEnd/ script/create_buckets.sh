MINIO_SKU_IMAGE_BUCKET_NAME=sku-image-bucket
MINIO_SPU_IMAGE_BUCKET_NAME=spu-image-bucket
MINIO_SPU_GALLERY_BUCKET_NAME=spu-gallery-bucket
MINIO_SPU_VIDEO_BUCKET_NAME=spu-video-bucket

mc alias set myminio http://localhost:9000 gmvnhiganadmin gmvnhiganadmin

mc mb myminio/$MINIO_SKU_IMAGE_BUCKET_NAME
mc mb myminio/$MINIO_SPU_IMAGE_BUCKET_NAME
mc mb myminio/$MINIO_SPU_GALLERY_BUCKET_NAME
mc mb myminio/$MINIO_SPU_VIDEO_BUCKET_NAME

echo "Đã tạo các bucket sau trong MinIO:"
echo "- $MINIO_SKU_IMAGE_BUCKET_NAME"
echo "- $MINIO_SPU_IMAGE_BUCKET_NAME"
echo "- $MINIO_SPU_GALLERY_BUCKET_NAME"
echo "- $MINIO_SPU_VIDEO_BUCKET_NAME"