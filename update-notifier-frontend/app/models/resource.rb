require 'active_record'
class Resource
  include ActiveRecord::Validations
  include ActiveModel::Validations::Callbacks

  #before_validation { url[0] = 'http://' unless url.start_with?('http://') }

  attr_accessor :name, :id, :user_id, :url, :tags, :dom_path, :filter, :schedule_code

  validates :name, presence: true
  validates :url, url: true, length: { maximum: 255 }

  def initialize(args = {})
    args.each_pair do |key, value|
      update_attribute(key, value)
    end
  end

  def update_attribute(key, value)
    #begin
      send "#{key}=", value
    #rescue NoMethodError => e
    #  p e
    #end
  end

  def new_record?
    @new_record
  end

  # def save, save! ?
end
