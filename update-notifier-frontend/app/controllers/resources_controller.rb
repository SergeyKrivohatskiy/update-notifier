class ResourcesController < ApplicationController

  def create
    # Create resource
    redirect_to action: :index
  end

  def index
    # 'Index' page - list of all resources and options
    @id = session[:user_id]
    @resources = DatabaseHelper.resources

  end

  def show
    # Display selected resource (with changes)
    # GET	/resources/:id
  end

  def update
    # Update resource
    redirect_to action: :index
  end

  def destroy
    # Delete resource
    # DELETE	/resources/:id
    redirect_to action: :index
  end
end
