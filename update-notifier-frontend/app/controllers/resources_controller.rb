class ResourcesController < ApplicationController
  include ResourcesHelper

  def create
    # Create resource
    resource = params[:resource]
    resource[:tags] = clean_tags(resource[:tags])
    @errors_array = DatabaseHelper.edit_resource(session[:user_id],
                                            nil, resource[:name],
                                            resource[:url], resource[:tags])
    redirect_to :back, flash: { errors: @errors_array }

  end

  def index
    @errors_array = flash[:errors]
    # 'Index' page - list of all resources and options
    @id = session[:user_id]
    @resources = DatabaseHelper.resources(@id)

  end

  def show
    # Display selected resource (with changes)
    # GET	/resources/:id
  end

  def update
    resource = params[:resource]
    resource[:tags] = clean_tags(resource[:tags])
    DatabaseHelper.edit_resource(session[:user_id], nil, resource[:name],
                                 resource[:url], resource[:tags])
    redirect_to action: :index
  end

  def destroy
    # Delete resource
    # DELETE	/resources/:id
    redirect_to action: :index
  end
end
