class AddPossessorToTemporaryLendLogs < ActiveRecord::Migration
  def change
    add_column :temporary_lend_logs, :possessors, :integer
  end
end
