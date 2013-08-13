# encoding: UTF-8
require 'httparty_wrapper'
require 'webrick/httpstatus'

module DatabaseHelper

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
  end

  def self.add_resource(resource)
    response = HTTPartyWrapper::post("#{resource.user_id}/resources", nil, resource)
    if WEBrick::HTTPStatus[response.code].new.
        kind_of? WEBrick::HTTPStatus::Success
      response.parsed_response.to_i
    else
      0
    end
  end

  def self.edit_resource(resource)
    # TODO It's stub. It, I think, should return boolean value - result of updating. Maybe error code
    if resource.valid?
      response = HTTPartyWrapper::put("#{resource.user_id}/resources/#{resource.id}", nil, resource)
s[response.code].new.
        kind_of? WEBrick::HTTPStatus::Success
      true
    else
      false
    end  end

  def self.get_resource(user_id, resource_id)
    resource_hash = HTTPartyWrapper::get("#{user_id}/resources/#{resource_id}", nil).parsed_response
    hashToResource(resource_hash)
  end

  def self.delete_resource(user_id, resource_id)
    response = HTTPartyWrapper::delete("#{user_id}/resources/#{resource_id}")
    WEBrick::HTTPStatus[response.code].new.kind_of? WEBrick::HTTPStatus::Success
  end

  def self.tags(user_id)
    response = HTTPartyWrapper::get("#{user_id}/tags")
    hashize(response.parsed_response)
  end

  def self.add_tag(user_id, name)
    response = HTTPartyWrapper::post("#{user_id}/tags", nil, name)
    if WEBrick::HTTPStatus[response.code].new.
        kind_of? WEBrick::HTTPStatus::Success
      response.parsed_response.to_i
    else
      0
    end
  end

  def self.delete_tag(user_id, tag_id)
    response = HTTPartyWrapper::delete("#{user_id}/tags/#{tag_id}")
    WEBrick::HTTPStatus[response.code].new.kind_of? WEBrick::HTTPStatus::Success
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
    array_of_hash.each do |hash_item|
      # hash is:
      # {
      #   'id': 1
      #   'name': 'some_name'
      # }
      # hash.shift is ['id', 1]
      key = hash_item.shift.last
      value = hash_item.shift.last
      hash[key] = value
    end
    hash
  end

  def self.hashToResource(hash)
    mappings = {'url' => 'url', 'id' => 'id', 'scheduleCode' => 'schedule_code',
                'tags' => 'tags', 'userId' => 'user_id', 'domPath' => 'dom_path',
                'filter' => 'filter'}

    Resource.new(Hash[hash.map {|k, v| [mappings[k], v] }])
  end
end