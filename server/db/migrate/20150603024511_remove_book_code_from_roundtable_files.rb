class RemoveBookCodeFromRoundtableFiles < ActiveRecord::Migration
  def up
    remove_column :roundtable_files, :book_code
  end

  def down
    add_column :roundtable_files, :book_code, :string
  end
end
