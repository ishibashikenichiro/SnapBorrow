class CreateRecognizepicnames < ActiveRecord::Migration
  def change
    create_table :recognizepicnames do |t|
      t.integer :group_id
      t.integer :user_id
      t.string :picname
      t.timestamps
    end
  end
end
