function deleteImages() {
  var selectedInputs = $("input:checked");
  var selectedIds = [];
  selectedInputs
    .each(function() {
      selectedIds.push($(this).attr('id'));
    });

  if(selectedIds.length < 1) alert("No images selected");
  else
    $.post(context + "/delete",
        { names: selectedIds },
        function(response) {
          var errors = $('<ul>');
          $.each(response, function() {
            if("Ok" === this.status) {
              var element = document.getElementById(this.name);
              $(element).parent().parent().remove();
            }
            else
            console.log("NOT HERE");
            errors
            .append($('<li>'),
              { html: "Failed to remove " + this.name + ": " + this.status });
          });
    if(errors.length > 0)
      $('#error').empty().append(errors);
        },
          "json");
}

$(document).ready(function(){
  $('#delete').click(deleteImages);
});
