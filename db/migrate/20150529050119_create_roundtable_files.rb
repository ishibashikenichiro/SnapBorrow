class CreateRoundtableFiles < ActiveRecord::Migration
  def change
    create_table :roundtable_files do |t|
      t.references :user, :null => false
      t.references :roundtable, :null => false
      t.string :book_code, :limit => 13
      t.string :title, :limit => 256
      t.string :keywords, :limit => 256
      t.string :filename, :limit => 256

      t.timestamps
    end
    add_index :roundtable_files, :user_id
    add_index :roundtable_files, :roundtable_id
  end
end
