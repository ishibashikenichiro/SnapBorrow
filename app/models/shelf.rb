class Shelf < ActiveRecord::Base
  self.primary_keys = :shelf_id, :group_id
  belongs_to :book, :class_name => 'Group', :foreign_key => 'group_id'
  attr_accessible :delete_date, :group_id, :name, :shelf_id
end
