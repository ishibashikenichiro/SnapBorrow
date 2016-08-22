class CreateGroups < ActiveRecord::Migration
  def change
    create_table :groups do |t|
      t.string :name, :null => false, :limit => 40
      t.datetime :delete_date

      t.timestamps
    end
  end
end
