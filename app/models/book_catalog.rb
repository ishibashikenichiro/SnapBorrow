class BookCatalog < ActiveRecord::Base
  attr_accessible :authors, :book_code, :publication_date, :publisher, :title, :image, :group_id
end
