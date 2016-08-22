class CreateMembers < ActiveRecord::Migration
  def change
    create_table :members do |t|
      t.references :group, :null => false
      t.references :user, :null => false

      t.foreign_key :groups
      t.foreign_key :users

      t.timestamps
    end
    add_index :members, [:group_id, :user_id]
  end
end
