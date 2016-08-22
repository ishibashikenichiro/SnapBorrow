class EquipmentRegistrationController < ApplicationController
  
require 'open3'
require "fileutils"
after_filter :make

def make(groupid)
    @list = groupid.to_s
    @list += " " + Rails.root.join('recognizer').to_s
    @equipment_classes = Registerpicname.find_all_by_group_id(groupid)
    @equipment_classes.each.with_index(1) do |set, i|
      @list += " "+set.picname
      @list += " "+set.equipment_classes_id.to_s
    end
    puts @list
    svmmake = "makelib " + @list
    o, e, s = Open3.capture3(Rails.root.join('recognizer').join(svmmake).to_s)
    @error = e
    puts @error
end

def copy(from ,to)
    open(from){|input|
        open(to, "w"){|output|
            output.write(input.read)
        }
    }
end

 def getjson
   equipmentclassname=params[:_json].first[:equipmentname]
   equipmentnum=params[:_json].first[:registernumber]
   userid=params[:_json].first[:userid]
   groupid=params[:_json].first[:groupid]
  
     newequipmentclass=EquipmentClass.new
     newequipmentclass.group_id=groupid
     newequipmentclass.equipment_classes_name=equipmentclassname
     newequipmentclass.save

   Registerpicname.where(equipment_classes_id: nil).update_all(:equipment_classes_id => newequipmentclass.id) 

   equipmentinfo=params[:_json]
   
   equipmentinfo.each do |_json|
   newequipment=Equipment.new
   newequipment.equipment_class_id=newequipmentclass.id
   newequipment.equipment_location=_json["loc"]
   newequipment.unique_id=_json["id"]
   newequipment.equipment_recorder_id=_json["userid"]
   newequipment.descr=_json["desc"]
   newequipment.state=0
   
   newequipment.save
    end
    movepicdec = "*.jpg"
    @file2 = Dir.glob(Rails.root.join('5').join(movepicdec)).sort_by {|f| File.mtime(f)}.reverse[0]
    cpfilename = equipmentclassname.to_s
    cpfilename += "0.jpg"
    @cpfile = Rails.root.join('app').join('assets').join('images').join(cpfilename).to_s
    puts @cpfile
    puts "1"
    puts @file2
    copy(@file2, @cpfile)
    make(groupid)
  end



 def getpictures
  name=params[:NAME]
  userid=params[:USERID]
  groupid=params[:GROUPID]
  puts "File received NAME="+ name
      data=params[:file]
      uptime=DateTime.now
      uploadfilename=groupid + data.original_filename
      savepicname = Registerpicname.new(group_id: groupid, user_id: userid, picname: Rails.root.join('5').join(uploadfilename).to_s)
      savepicname.save

      File.open(Rails.root.join('5') + uploadfilename,'wb') do |of|
                               of.write(data.read)
  end
 end
end