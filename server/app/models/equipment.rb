class Equipment < ActiveRecord::Base
  # attr_accessible :title, :body
  attr_accessible :id, :equipment_class_id, :equipment_location, :unique_id, :equipment_record_date, :equipment_recorder_id, :descr, :state
end
