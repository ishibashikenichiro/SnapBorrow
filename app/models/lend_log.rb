class LendLog < ActiveRecord::Base
  belongs_to :book, :class_name => 'Book', :foreign_key => 'book_id'
  belongs_to :user, :class_name => 'User', :foreign_key => 'user_id'
  belongs_to :catalog, :class_name => 'BookCatalog'
  attr_accessible :book_id, :cancel_date, :lend_date, :return_date, :user_id, :temporary, :book_code
end
