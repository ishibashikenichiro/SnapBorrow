class EquipmentClass < ActiveRecord::Base
  # attr_accessible :title, :body
  attr_accessible :id, :group_id, :equipment_classes_name, :recognizer_number
end
