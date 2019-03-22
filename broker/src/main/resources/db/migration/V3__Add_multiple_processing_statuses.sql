ALTER TABLE message
RENAME COLUMN received TO received_from_producer;

ALTER TABLE message
ADD COLUMN processing_status VARCHAR(20) NOT NULL DEFAULT 'NOT_PROCESSED';

ALTER TABLE message
ADD COLUMN consumer_id VARCHAR(20);

ALTER TABLE message
ADD COLUMN sent_to_consumer TIMESTAMP;

ALTER TABLE message
ADD COLUMN confirmed_from_consumer TIMESTAMP;

UPDATE message
SET processing_status = 'PROCESSED', confirmed_from_consumer = NOW()
WHERE processed = TRUE;

ALTER TABLE message
DROP COLUMN processed;