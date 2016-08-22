class EquipmentLog < ActiveRecord::Base
  # attr_accessible :title, :body
  attr_accessible :equipment_id, :user_id, :unique_id, :equipment_lend_date, :equipment_return_date
end
