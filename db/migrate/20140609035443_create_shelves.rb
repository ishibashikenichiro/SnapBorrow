class CreateShelves < ActiveRecord::Migration
  def change
    create_table :shelves  do |t|
      t.integer :shelf_id, :null => false
      t.references :group, :null => false
      t.foreign_key :groups

      t.string :name, :null => false, :limit => 40
      t.datetime :delete_date

      t.timestamps

    end
    add_index :shelves, [:shelf_id, :group_id], :unique => true
  end

end
