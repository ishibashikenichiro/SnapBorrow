class AddUploadtimeToRoundtableFiles < ActiveRecord::Migration
#  def change
#    add_column :roundtable_files, :uploadtime, :datetime
#  end
  def up
	add_column :roundtable_files, :uploadtime, :datetime, :null => false
  end

  def down
    remove_column :roundtable_files, :uploadtime
  end
end
