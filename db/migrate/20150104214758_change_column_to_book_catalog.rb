class ChangeColumnToBookCatalog < ActiveRecord::Migration
  def up
    change_column :book_catalogs,  :authors, :string, :null => true, :limit => 100
    change_column :book_catalogs,  :publisher, :string,  :null => true, :limit => 40
    change_column :book_catalogs,  :publication_date, :datetime,  :null => true
  end

  def down
    change_column :book_catalogs,  :authors, :string, :null => false, :limit => 100
    change_column :book_catalogs,  :publisher, :string,  :null => false, :limit => 40
    change_column :book_catalogs,  :publication_date, :datetime,  :null => false
  end
end
