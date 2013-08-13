class ResourcesController < ApplicationController
  include ResourcesHelper

  def create
    # Create resource
    resource_info = params[:resource]
    resource = resourceInfoToResource(resource_info)

    if resource.valid?
      DatabaseHelper.add_resource(resource)
    else
      @errors_array = resource.errors.full_messages
    end
    redirect_to :back, flash: { errors: @errors_array }

  end

  def index
    # 'Index' page - list of all resources and options
    @errors_array = flash[:errors]
    @id = session[:user_id]
    session[:tags] = @tags = DatabaseHelper.tags(@id)
    @resources = DatabaseHelper.resources(@id)
    @resources
  end

  def show
    # Display selected resource_info (with changes)
    # GET	/resources/:tag_id
  end

  def edit
    resource_id = params[:id]
    @resource = DatabaseHelper.get_resource(session[:user_id],resource_id)

    all_tags = DatabaseHelper.tags(session[:user_id])
    tags = all_tags.map do |key, value|
      value if @resource.tags.include? key
    end
    @tag_string = tags.compact.inject('') do |string, tag_name|
      string+"#{tag_name}; "
    end
  end

  def update
    resource_info = params[:resource]
    resource_info[:id] = params[:id]
    DatabaseHelper.edit_resource(resourceInfoToResource(resource_info))
    redirect_to action: :index
  end

  def destroy
    result = DatabaseHelper.delete_resource(session[:user_id], params[:id])
    redirect_to action: :index
  end

  def resourceInfoToResource(resource_info)
    resource_info[:tags] = clean_tags(resource_info[:tags])

    # tags - all user tags ({ id: name })
    tags = session[:tags]
    # new_tags - resource tags, which no exist in db (only names)
    new_tags = resource_info[:tags] - tags.values
    # old_tags - resource tags, which already exist in db (only names)
    old_tags = resource_info[:tags] - new_tags
    # another way with the same result:
    # old_tags = resource_info[:tags] & tags.values

    tag_ids = tags.map do |key, value|
      key if old_tags.include? value
    end

    tag_ids.compact!

    new_tags.each do |tag|
      tag_id = DatabaseHelper.add_tag(session[:user_id], tag).to_i
      tag_ids.push(tag_id) if tag_id > 0
    end

    resource_info[:tags] = tag_ids

    resource = Resource.new(resource_info)
    resource.user_id = session[:user_id]
    resource.schedule_code = 0
    resource.dom_path = '/'
    resource
  end
end
