-- First, drop any existing foreign key constraints
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing notifications table
DROP TABLE IF EXISTS notifications;

-- Recreate notifications table
-- The notifications table should NOT have a recipient_username column. Only related_user_name is used in code and entity.
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    related_booking_id BIGINT,
    related_user_name VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1; 