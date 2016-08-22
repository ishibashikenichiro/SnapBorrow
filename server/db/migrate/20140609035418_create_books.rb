class CreateBooks < ActiveRecord::Migration
  def change
    create_table :books do |t|
      t.string :book_code, :null => false, :limit => 13
      t.integer :group_id, :null => false
      t.integer :shelf_id, :null => false
      t.datetime :delete_date
      t.boolean :state, :null => false, :default => false

      t.timestamps
    end
  end
end
