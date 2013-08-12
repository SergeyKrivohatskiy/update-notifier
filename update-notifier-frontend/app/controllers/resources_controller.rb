class ResourcesController < ApplicationController
  include ResourcesHelper

  def create
    # Create resource
    resource_info = params[:resource]
    resource_info[:tags] = clean_tags(resource_info[:tags])

    tags = session[:tags]
    new_tags = resource_info[:tags] - tags.values

    hash = {}
    new_tags.each do |tag|
      tag_id = DatabaseHelper.add_tag(session[:user_id], tag)
      hash[tag_id] = tag
    end

    resource_info[:tags] = hash.keys.map { |key_string| key_string.to_i }

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
    session[:tags] = @tags = DatabaseHelper.tags(@id)
    @resources = DatabaseHelper.resources(@id)
    p @resources
    @resources

  end

  def show
    # Display selected resource_info (with changes)
    # GET	/resources/:tag_id
  end

  def edit
    id = params[:id]
    redirect_to action: :index
  end

  def destroy
    DatabaseHelper.delete_resource(session[:user_id], params[:id])
    redirect_to action: :index
  end
end
