class NotificationLog < ActiveRecord::Base
  attr_accessible :date, :is_notify, :message, :user_id
end
