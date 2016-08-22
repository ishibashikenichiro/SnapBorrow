class Registerpicname < ActiveRecord::Base
  # attr_accessible :title, :body
  attr_accessible :id, :group_id, :user_id, :equipment_classes_id, :picname
end
