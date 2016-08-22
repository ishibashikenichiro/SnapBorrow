class CreatePasswordResets < ActiveRecord::Migration
  def change
    create_table :password_resets do |t|
      t.integer :user_id, :null => false
      t.string :url_hash, :null => false
      t.datetime :expire_at, :null => false

      t.timestamps
    end
  end
end
