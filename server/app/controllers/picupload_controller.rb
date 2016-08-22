class PicuploadController<ApplicationController
 def  oneupload
	name=params[:NAME]
             userid=params[:USERID]
             groupid=params[:GROUPID]
	puts "File received NAME="+ name
      data=params[:file]
      uptime=DateTime.now
      uploadfilename=data.original_filename

      File.open(Rails.root.join('recognition',uploadfilename),'wb') do |of|
                               of.write(data.read)
  end
 end
end