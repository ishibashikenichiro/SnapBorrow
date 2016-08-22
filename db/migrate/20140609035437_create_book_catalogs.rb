class CreateBookCatalogs < ActiveRecord::Migration
  def change
    create_table :book_catalogs do |t|
      t.string :book_code, :null => false, :limit => 13
      t.string :title, :null => false, :limit => 100
      t.string :authors, :null => false, :limit => 100
      t.string :publisher, :null => false, :limit => 40
      t.datetime :publication_date, :null => false

      t.timestamps
    end
    add_index :book_catalogs, :book_code, :unique => false
  end
end
