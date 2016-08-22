class CreateRegisterpicnames < ActiveRecord::Migration
  def change
    create_table :registerpicnames do |t|
	 t.integer :group_id
      t.integer :user_id
      t.integer :equipment_classes_id
      t.string :picname
      t.timestamps

      t.timestamps
    end
  end
end
