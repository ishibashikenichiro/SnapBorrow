class Book < ActiveRecord::Base
  belongs_to :group, :class_name => 'Group', :foreign_key => 'group_id'
  belongs_to :catalog, :class_name => 'BookCatalog'
  belongs_to :shelf, :class_name => 'Shelf', :foreign_key => ['shelf_id', 'group_id']
  has_many :lend_log
  attr_accessible :book_code, :delete_date, :group_id, :shelf_id, :state
end
