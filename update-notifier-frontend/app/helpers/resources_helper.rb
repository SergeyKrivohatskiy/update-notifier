module ResourcesHelper
  def clean_tags(tags_string)
    unless tags_string.empty?
      tags = tags_string.split(';').map do |dirty_tag|
        dirty_tag.strip
      end
      tags[-1][-1] = '' if tags[-1].ends_with?(';')
      tags
    else
      []
    end
  end
end
