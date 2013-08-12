# encoding: UTF-8
require 'httparty_wrapper'
require 'webrick/httpstatus'

module DatabaseHelper

  #@resources = [Resource.new(name: 'Гогле', url: 'http://google.ru', tags: %w[search, favorite, GDG]),
  #              Resource.new(name: 'Яndex', url: 'http://yandex.ru', tags: %w[search]),
  #              Resource.new(name: 'Thumbtack', url: 'http://thumbtack.net', tags: %w[favorite it development]),
  #              Resource.new(name: 'ИСС Арт', url: 'http://issart.ru', tags: %w[it development])]

  def self.sign_in(email)
    response = HTTPartyWrapper::get('signin', { email: email })
    if WEBrick::HTTPStatus[response.code].new.
        kind_of? WEBrick::HTTPStatus::Success
      response.parsed_response.to_i
    else
      0
    end
  end

  def self.resources(user_id)
    response = HTTPartyWrapper::get("#{user_id}/resources")
    symbolize(response.parsed_response)
    #return @resources
  end

  def self.add_resource(resource)
    response = HTTPartyWrapper::post("#{resource.user_id}/resources", nil, resource)
    if WEBrick::HTTPStatus[response.code].new.
        kind_of? WEBrick::HTTPStatus::Success
      response.parsed_response
    else
      '0'
    end
  end

  def self.edit_resource()
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    resource = Resource.new(name: name, url: url, tags: tags)
    if resource.valid?
      #response = HTTPartyWrapper::post('resource', user_id, user_id: resource_id, name: name, url: url,
      #                tags: tags)
      @resources.push Resource.new(name: name, url: url, tags: tags)
    end
    resource.errors.full_messages
  end

  def self.delete_resource(user_id, resource_id)
    # TODO It' stub. Return boolean !
    #response = HTTPartyWrapper::delete('resource', resource_id)
    rand(0..1) == 0
  end

  def self.tags(user_id)
    response = HTTPartyWrapper::get("#{user_id}/tags")
    hashize(response.parsed_response)
  end

  def self.add_tag(user_id, name)
    response = HTTPartyWrapper::post("#{user_id}/tags", nil, name)
    response.parsed_response
  end

  private
  def self.symbolize(array_of_hash)
    return [] if array_of_hash.nil?
    array_of_hash.map do |hash|
      hash.inject({}){|memo,(k,v)| memo[k.to_sym] = v; memo}
    end
  end
  def self.hashize(array_of_hash)
    return {} if array_of_hash.nil?
    hash = {}
    array_of_hash.each do |hash|
      # hash is:
      # {
      #   'id': 1
      #   'name': 'some_name'
      # }
      # hash.shift is ['id', 1]
      key = hash.shift.first
      value = hash.shift.first
      hash[key] = value
    end
    hash
  end

end