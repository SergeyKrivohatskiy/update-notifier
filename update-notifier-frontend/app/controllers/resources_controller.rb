class ResourcesController < ApplicationController
  include ResourcesHelper

  def create
    # Create resource
    resource_info = params[:resource]
    resource_info[:tags] = clean_tags(resource_info[:tags])
    resource = Resource.new(resource_info)
    resource.user_id = session[:user_id]
    resource.shedule_code = 0
    resource.dom_path = '/'

    @errors_array = DatabaseHelper.add_resource(resource)
    redirect_to :back, flash: { errors: @errors_array }

  end

  def index
    # 'Index' page - list of all resources and options
    @errors_array = flash[:errors]
    @id = session[:user_id]
    @resources = DatabaseHelper.resources(@id)
    p @resources
    @resources

  end

  def show
    # Display selected resource_info (with changes)
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
    # Delete resource_info
    # DELETE	/resources/:id
    redirect_to action: :index
  end
end
