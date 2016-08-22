class InvitationLog < ActiveRecord::Base
  attr_accessible :expire_at, :group_id, :mail, :url_hash
end
