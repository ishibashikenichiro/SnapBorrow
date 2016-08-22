class PasswordReset < ActiveRecord::Base
  attr_accessible :expire_at, :url_hash, :user_id
end
