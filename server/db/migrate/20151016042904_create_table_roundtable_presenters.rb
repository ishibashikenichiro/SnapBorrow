class CreateTableRoundtablePresenters < ActiveRecord::Migration
  def up
      create_table :roundtable_presenters do |t|
         t.references :roundtable, :null => false
         t.references :user, :null => false
         t.foreign_key :roundtables
         t.foreign_key :users
         t.timestamps
        end
	add_index :roundtable_presenters, [:roundtable_id, :user_id]	
  end

  def down
	drop_table :roundtable_presenters
  end
end
