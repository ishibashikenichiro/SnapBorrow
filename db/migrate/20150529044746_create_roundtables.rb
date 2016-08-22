class CreateRoundtables < ActiveRecord::Migration
  def change
    create_table :roundtables do |t|
      t.references :group, :null => false
      t.string :title, :null => false, :limit => 128
      t.string :keywords, :limit => 256
      t.string :book_code, :limit => 13
      t.datetime :startdatetime
      t.datetime :enddatetime
      t.string :place, :limit => 40

      t.timestamps
    end
    add_index :roundtables, :group_id
  end
end
