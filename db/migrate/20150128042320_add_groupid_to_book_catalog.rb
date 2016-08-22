class AddGroupidToBookCatalog < ActiveRecord::Migration
  def change
    add_column :book_catalogs, :group_id, :integer, :null => false
    change_column :book_catalogs, :book_code, :string, :null => true, :limit => 13
  end
end
