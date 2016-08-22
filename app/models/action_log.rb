class ActionLog < ActiveRecord::Base
  attr_accessible :action, :date, :device, :group_id, :user_id
end
