require 'active_record'
class Resource
  include ActiveRecord::Validations
  include ActiveModel::Validations::Callbacks

  #before_validation { url[0] = 'http://' unless url.start_with?('http://') }

  attr_accessor :name, :url, :tags

  validates :name, presence: true
  validates :url, url: true, length: { maximum: 255 }

  def initialize(args = {})
    args.each_pair do |key, value|
      update_attribute(key, value)
    end
  end

  def update_attribute(key, value)
    send "#{key}=", value
  end

  def new_record?
    @new_record
  end

  # def save, save! ?
end
