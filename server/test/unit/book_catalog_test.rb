require 'test_helper'

class BookCatalogTest < ActiveSupport::TestCase
  test "should create new Book_catalog" do
    assert(BookCatalog.create(
                              :book_code => "4444444444444",
                              :title => "book3",
                              :authors => "author4",
                              :publisher => "publisher3",
                              :publication_date => "2014-04-01",
                              :group_id => 0), "")
  end

  test "should not create Book_catalog dupulicate book_code" do
    assert(BookCatalog.create(
                              :book_code => "1111111111111",
                              :title => "book3",
                              :authors => "author4",
                              :publisher => "publisher3",
                              :publication_date => "2014-04-01",
                              :group_id => 0))
  end



end
