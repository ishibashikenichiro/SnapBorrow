class CreateInvitationLogs < ActiveRecord::Migration
  def change
    create_table :invitation_logs do |t|
      t.references :group, :null => false
      t.string :mail
      t.datetime :expire_at
      t.string :url_hash

      t.foreign_key :groups

      t.timestamps
    end
  end
end
