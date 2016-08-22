class CreateManagers < ActiveRecord::Migration
  def change
    create_table :managers do |t|
      t.references :group, :null => false
      t.references :user, :null => false

      t.foreign_key :groups
      t.foreign_key :users

      t.timestamps
    end
    add_index :managers, [:group_id, :user_id], :unique => true
  end
end
