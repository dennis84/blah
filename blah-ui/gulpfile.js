var gulp = require('gulp')
var gutil = require('gulp-util')
var browserify = require('browserify')
var babelify = require('babelify')
var watchify = require('watchify')
var source = require('vinyl-source-stream')
var streamify = require('gulp-streamify')
var stylus = require('gulp-stylus')
var nib = require('nib')

var watch = false

function onError(error) {
  gutil.log(gutil.colors.red(error.message))
}

gulp.task('js', function() {
  var bundleStream = browserify('./src/js/index.js')
    .transform(babelify)

  if(true === watch) {
    bundleStream = watchify(bundleStream)
  }

  bundleStream
    .on('update', rebundle)
    .on('log', gutil.log)

  function rebundle() {
    return bundleStream
      .bundle()
      .on('error', onError)
      .pipe(source('index.js'))
      .pipe(gulp.dest('./assets/js'))
  }

  return rebundle()
})

gulp.task('css', function() {
  return gulp.src('src/css/index.styl')
    .pipe(stylus({
      compress: true,
      use: [nib()]
    }))
    .pipe(gulp.dest('./assets/css'))
})

gulp.task('watch', function() {
  watch = true
  gulp.watch('src/css/**', ['css'])
  gulp.watch('components/**', ['copy'])
})

gulp.task('copy', function() {
  gulp.src('./node_modules/bulma/css/bulma.css')
    .pipe(gulp.dest('./assets/css'))
  gulp.src('./node_modules/pikaday/css/pikaday.css')
    .pipe(gulp.dest('./assets/css'))
  gulp.src('./components/worldmap/worldmap.js')
    .pipe(gulp.dest('./assets/js'))
  gulp.src('./components/people/people.js')
    .pipe(gulp.dest('./assets/js'))
  gulp.src('./components/jobs/jobs.js')
    .pipe(gulp.dest('./assets/js'))
  gulp.src('./components/most-viewed/most-viewed.js')
    .pipe(gulp.dest('./assets/js'))
  gulp.src('./components/referrer/referrer.js')
    .pipe(gulp.dest('./assets/js'))
  gulp.src('./components/sum/sum.js')
    .pipe(gulp.dest('./assets/js'))
})

gulp.task('build', ['css', 'js', 'copy'])
gulp.task('default', ['watch', 'css', 'js', 'copy'])
