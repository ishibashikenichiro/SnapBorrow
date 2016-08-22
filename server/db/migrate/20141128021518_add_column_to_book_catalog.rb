class AddColumnToBookCatalog < ActiveRecord::Migration
  def change
    add_column :book_catalogs, :image, :string
  end
end
